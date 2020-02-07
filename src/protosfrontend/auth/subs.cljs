(ns protosfrontend.auth.subs
    (:require
        [re-frame.core :as rf]))

(rf/reg-sub
  :alert-login
  (fn alert-login-sub
    [db _]
      (-> db
          :login
          :alert)))
