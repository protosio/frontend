(ns protosfrontend.auth.events
    (:require
        [re-frame.core :as rf]
        [protosfrontend.util :as util]))

(rf/reg-event-fx
  :auth-failure
  (fn init-failure-handler
    [{db :db} [_ result]]
    {:db (assoc-in db [:login :alert] {:type "danger" :message (get-in result [:response :error])})}))

(rf/reg-event-fx
  :login-success
  (fn login-success-handler
    [{db :db} [_ result]]
    {:dispatch-n [[:save-auth result]]
     :redirect-to (if (:previous-page db)
                      (:previous-page db)
                      [:dashboard-page])
     :init-ws []
     :db (-> db
             (assoc-in [:previous-page] nil)
             (assoc-in [:auth :userinfo] (dissoc result :token))
             (assoc :login nil))}))

(rf/reg-event-fx
  :load-userinfo
  [(rf/inject-cofx :storage/get {:name :userinfo})]
  (fn load-userinfo-handler
    [{db :db userinfo :storage/get} _]
    {:db (assoc-in db [:auth :userinfo] (js->clj (.parse js/JSON userinfo) :keywordize-keys true))}))

(rf/reg-event-fx
  :save-auth
  (fn save-auth-handler
    [_ [_ result]]
    {:storage/set {:name :userinfo :value (.stringify js/JSON (clj->js (dissoc result :token)))}}))

(rf/reg-event-fx
  :login
  (fn login-handler
    [{db :db} _]
    {:dispatch [:http-post {:url (util/createurl ["auth" "login"])
                            :on-success [:login-success]
                            :on-failure [:auth-failure]
                            :post-data (get-in db [:login :form])}]}))

(rf/reg-event-fx
  :logout
  (fn logout-handler
    [_ [_ _]]
    {:dispatch [:http-post {:url (util/createurl ["auth" "logout"])
                :on-success [:logout-success]
                :on-failure [:auth-failure]
                :post-data {}}]}))

(rf/reg-event-fx
 :logout-success
 (fn logout-success-handler
   [{db :db} _]
   {:redirect-to [:login-page]
    :storage/remove {:name :userinfo}
    :db (assoc db :auth nil)}))
