(ns auth.events
    (:require
        [re-frame.core :as rf]
        [clairvoyant.core :refer-macros [trace-forms]]
        [protosfrontend.events :as pe]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "green")}

(rf/reg-event-fx
  :login-failure
  (fn init-failure-handler
    [{db :db} [_ result]]
    {:db (assoc-in db [:login :alert] {:type "danger" :message (get-in result [:response :error])})}))

(rf/reg-event-fx
  :login-success
  (fn login-success-handler
    [{db :db} [_ result]]
    {:dispatch-n [[:save-auth result]
                  (vec (flatten [:redirect-to (:previous-page db)]))]
     :db (-> db
             (assoc-in [:previous-page] nil)
             (assoc-in [:auth] result)
             (assoc-in [:login :alert] {:type "success" :message "Login successful"}))}))

(rf/reg-event-fx
  :load-username
  [(rf/inject-cofx :storage/get {:name :username})]
  (fn load-username-handler
    [{db :db username :storage/get} _]
    {:db (assoc-in db [:auth :username] username)}))

(rf/reg-event-fx
  :save-auth
  (fn save-auth-handler
    [_ [_ result]]
    {:cookie/set {:name "token"
                  :value (:token result)
                  :on-success [:noop]
                  :on-failure [:noop]}
    :storage/set {:name :username :value (:username result)}}))

(rf/reg-event-fx
  :login
  (fn login-handler
    [{db :db} _]
    {:dispatch [:http-post {:url (pe/createurl ["auth" "login"])
                            :on-success [:login-success]
                            :on-failure [:login-failure]
                            :post-data (get-in db [:login :form])}]}))

(rf/reg-event-fx
  :logout
  (fn logout-handler
    [{db :db} [_ result]]
    {:cookie/remove {:name "token"
                     :on-success [:noop]
                     :on-failure [:noop]}
    :storage/remove {:name :username}
    :db (assoc db :auth nil)}))

)