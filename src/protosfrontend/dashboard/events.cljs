(ns dashboard.events
    (:require
        [clojure.string :as string]
        [ajax.core :as ajax]
        [re-frame.core :as rf]
        [day8.re-frame.http-fx]
        [protosfrontend.events :as pe]
        [com.smxemail.re-frame-cookie-fx]
        [com.degel.re-frame.storage]
        [district0x.re-frame.interval-fx]
        [clairvoyant.core :refer-macros [trace-forms]]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "green")}

;; -- Common operations ----------------------------------------

(rf/reg-event-fx
  :dashboard-failure
  (fn dashboard-failure-handler
    [{db :db} [_ result]]
    {:db (assoc-in db [:alert] {:type "danger" :message (get-in result [:response :error])})}))

(rf/reg-event-db
  :save-response
  (fn save-response-handler
    [db [_ path result]]
    (assoc-in db path result)))

;; -- Installers -----------------------------------------------

(rf/reg-event-fx
  :get-installer
  (fn get-installer-handler
    [{db :db} [_ installer-id]]
    {:dispatch [:http-get {:url (pe/createurl ["e" "installers" installer-id])
                           :on-success [:save-response [:installers (keyword installer-id)]]
                           :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :create-installer-metadata
  (fn create-installer-metadata-handler
    [{db :db} [_ installer-id]]
    {:dispatch [:http-post {:url (pe/createurl ["e" "installers" installer-id "metadata"])
                            :response-format :raw
                            :on-success [:get-installer installer-id]}]
     :db db}))

(rf/reg-event-fx
  :get-installers
  (fn get-installers-handler
    [{db :db} _]
    {:dispatch [:http-get {:url (pe/createurl ["e" "installers"])
                           :on-success [:save-response [:installers]]
                           :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :remove-installer
  (fn remove-installer-handler
    [{db :db} [_ installer-id]]
    {:dispatch [:http-delete {:url (pe/createurl ["e" "installers" installer-id])
                              :response-format :raw
                              :on-success [:set-active-page [:installers-page] [:get-installers]]}]
      :db db}))

;; -- Apps -----------------------------------------------------

(rf/reg-event-fx
  :get-apps
  (fn get-apps-handler
    [{db :db} _]
    {:dispatch [:http-get {:url (pe/createurl ["e" "apps"])
                           :on-success [:save-response [:apps]]
                           :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :get-app
  (fn get-app-handler
    [{db :db} [_ app-id]]
    {:dispatch [:http-get {:url (pe/createurl ["e" "apps" app-id])
                           :on-success [:save-response [:apps (keyword app-id)]]
                           :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :create-app
  (fn create-app-handler
    [{db :db} [_ _]]
    {:dispatch [:http-post {:url (pe/createurl ["e" "apps"])
                            :on-success [:set-active-page [:apps-page] [:get-apps]]}]
     :db db}))

(rf/reg-event-fx
  :remove-app
  (fn remove-app-handler
    [{db :db} [_ app-id]]
    {:dispatch [:http-delete {:url (pe/createurl ["e" "apps" app-id])
                              :response-format :raw
                              :on-success [:set-active-page [:apps-page] [:get-apps]]}]
     :db db}))

(rf/reg-event-fx
  :app-state
  (fn app-state-handler
    [{db :db} [_ app-id state]]
    {:dispatch [:http-post {:url (pe/createurl ["e" "apps" app-id "action"])
                            :response-format :raw
                            :on-success [:set-active-page [:app-page app-id] [:get-app app-id]]
                            :post-data {:name state}}]
     :db db}))

;; -- Resources ------------------------------------------------

(rf/reg-event-fx
  :get-resources
  (fn get-resources
    [{db :db} _]
    {:dispatch [:http-get {:url (pe/createurl ["e" "resources"])
                           :on-success [:save-response [:resources]]
                           :on-failure [:dashboard-failure]}]}))

)