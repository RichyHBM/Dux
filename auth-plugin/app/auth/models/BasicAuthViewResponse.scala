package auth.models

import common.models.BasicViewResponse

class BasicAuthViewResponse(title: String, val user: AuthenticatedUser)
    extends BasicViewResponse(title)
