package com.bstcine.h5.ui.login

import com.bstcine.h5.base.BasePresenter
import com.bstcine.h5.base.BaseView

interface LoginContract {

    interface View : BaseView<Presenter> {

        fun onLoginSuccess(token: String, user: Map<*, *>)

        fun onLoginSuccessWithoutRx(token: String, user: Map<*, *>)

    }

    interface Presenter : BasePresenter {

        fun login(username: String, password: String)

    }
}