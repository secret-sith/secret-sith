const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

const db = admin.firestore();

exports.createUserInDatabase = functions.auth.user().onCreate((user) => {
  const email = user.email; // The email of the user.
  const name = user.displayName || email; // The display name of the user.
  const uuid = user.uid;
  console.log(`UUID: ${uuid}`);

  return db
    .collection("users")
    .doc(uuid)
    .set({
      name,
      username: name,
      email,
      createdAt: new Date().toISOString(),
    })
    .then((writeResult) => {
      console.log("created user");
      return writeResult;
    })
    .catch((error) => console.log(error));
});

exports.addUsersAsFriends = functions.firestore
  .document("friendships/{friendshipId}")
  .onUpdate((change, context) => {
    const newValue = change.after.data();
    const previousValue = change.before.data();

    const { state: newState, friend, user } = newValue;
    const { state: oldState } = previousValue;

    if (oldState !== "friends" && newState === "friends") {
      return db
        .runTransaction((transaction) => {
          return transaction
            .getAll(user, friend)
            .then(([userDoc, friendDoc]) => {
              transaction.update(user, {
                friends: admin.firestore.FieldValue.arrayUnion(friend),
              });
              transaction.update(friend, {
                friends: admin.firestore.FieldValue.arrayUnion(user),
              });
              return Promise.resolve();
            });
        })
        .then((result) => {
          console.log("Transaction successfully committed!");
          return Promise.resolve("");
        })
        .catch((error) => {
          console.log("Transaction failed: ", error);
        });
    }

    return Promise.resolve();
  });

exports.createPlayerInvite = functions.firestore
  .document("games/{gamesId}/players/{playerId}")
  .onCreate((player, context) => {
    const data = player.data();
    const gameId = context.params.gamesId;
    const playerId = context.params.playerId;
    return db.collection("invites").add({
      gameId,
      from: data.inviteBy,
      fromId: data.inviteById,
      to: data.user,
      timestamp: data.createdAt,
      state: "pending",
      playerId,
    });
  });

exports.updateUserStartedGame = functions.firestore
  .document("games/{gamesId}")
  .onCreate((game, context) => {
    const data = game.data();
    const gameId = context.params.gamesId;
    return db.collection("users").doc(data.host).set(
      {
        currentGame: gameId,
      },
      { merge: true }
    );
  });

exports.userAcceptOrDeclineInvite = functions.firestore
  .document("invites/{invitesId}")
  .onUpdate((change, context) => {
    const newValue = change.after.data();
    const invitesId = context.params.invitesId;
    const { state, gameId, playerId, isHost, to } = newValue;

    if (isHost) {
      return Promise.resolve();
    }

    if (state === "accepted" || state === "declined") {
      return db
        .collection("games")
        .doc(gameId)
        .collection("players")
        .doc(playerId)
        .set(
          {
            state: state,
          },
          { merge: true }
        )
        .then(() => {
          return Promise.all([
            db
              .collection("users")
              .doc(to)
              .set({ currentGame: gameId }, { merge: true }),
            db.collection("invites").doc(invitesId).delete(),
          ]);
        });
    } else {
      return Promise.resolve();
    }
  });

exports.onGamePhaseNominateChancellor = functions.firestore
  .document("games/{gameId}")
  .onUpdate((change, context) => {
    if (change.before.get("phase") === "nominate_chancellor") {
      return Promise.resolve();
    }
    if (change.after.get("phase") !== "nominate_chancellor") {
      return Promise.resolve();
    }
    const gameRef = change.after.ref
    const presidentialCandidateRef = change.after.get("presidentialCandidate");
    return db.runTransaction((transaction) => {
      return transaction
        .get(gameRef.collection("players").orderBy("index"))
        .then(players => {
          const playerRefs = players.docs.map(it => it.ref)
          const oldIndex = playerRefs.findIndex(it => it.isEqual(presidentialCandidateRef))
          const newIndex = (oldIndex + 1) % playerRefs.length
          const newPlayerRef = playerRefs[newIndex]
          return transaction.update(gameRef, {
            presidentialCandidate: newPlayerRef,
            chancellorCandidate: null,
          })
        })
    })
  });
