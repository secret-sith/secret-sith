package com.secret.palpatine.data.model.game

import com.secret.palpatine.R

/**
  * Describes the available types of policies.
 *
 * loyalist --> Good Policies
 * imperialist --> Evil Policies
 */
enum class PolicyType(val drawableResource: Int) {
    loyalist(R.drawable.policy_good),
    imperialist(R.drawable.policy_evil),

}