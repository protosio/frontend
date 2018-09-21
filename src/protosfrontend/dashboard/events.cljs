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
    {:db (assoc-in db [:dashboard :alert] {:type "danger" :message (get-in result [:response :error])})}))

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
                            :on-success [:get-installer installer-id]}]}))

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
                              :on-success [:set-active-page [:installers-page] [:get-installers]]}]}))

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
    [{db :db} [_ installer-id selected-version]]
    {:dispatch [:http-post {:url (pe/createurl ["e" "apps"])
                            :on-success [:create-app-success]
                            :on-failure [:dashboard-failure]
                            :post-data {:installer-id installer-id :installer-version selected-version :name (get-in db [:create-app :form :name]) :installer-params (get-in db [:create-app :form :installer-params])}}]}))

(rf/reg-event-fx
  :create-app-success
  (fn create-app-success-handler
    [{db :db} [_ result]]
    {:dispatch [:set-active-page [:apps-page] [:get-apps]]
     :db (assoc-in db [:dashboard :alert] {:type "success" :message (str "App created")})}))

(rf/reg-event-fx
  :remove-app
  (fn remove-app-handler
    [{db :db} [_ app-id]]
    {:dispatch [:http-delete {:url (pe/createurl ["e" "apps" app-id])
                              :on-success [:remove-app-success app-id]
                              :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :remove-app-success
  (fn remove-app-success-handler
    [{db :db} [_ app-id]]
    {:dispatch [:set-active-page [:apps-page] [:get-apps]]
     :db (assoc-in db [:dashboard :alert] {:type "success" :message (str "App " app-id " removed")})}))

(rf/reg-event-fx
  :app-state
  (fn app-state-handler
    [{db :db} [_ app-id state]]
    {:dispatch [:http-post {:url (pe/createurl ["e" "apps" app-id "action"])
                            :on-success [:app-state-success app-id state]
                            :on-failure [:dashboard-failure]
                            :post-data {:name state}}]}))

(rf/reg-event-fx
  :app-state-success
  (fn app-state-success-handler
    [{db :db} [_ app-id state]]
    {:dispatch [:get-app app-id]
     :db (assoc-in db [:dashboard :alert] {:type "success" :message (str "App " app-id " set to state [" state "]")})}))

;; -- Resources ------------------------------------------------

(rf/reg-event-fx
  :get-resources
  (fn get-resources
    [{db :db} _]
    {:dispatch [:http-get {:url (pe/createurl ["e" "resources"])
                           :on-success [:save-response [:resources]]
                           :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :remove-resource
  (fn remove-resource-handler
    [{db :db} [_ id]]
    {:dispatch [:http-delete {:url (pe/createurl ["e" "resources" id])
                              :on-success [:remove-resource-success]
                              :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :remove-resource-success
  (fn remove-resource-success-handler
    [{db :db} _]
    {:dispatch [:set-active-page [:resources-page] [:get-resources]]}))

;; -- App store ------------------------------------------------

(rf/reg-event-fx
  :get-appstore-all
  (fn get-appstore-all-handler
    [_ _]
    {:dispatch [:http-get {:url (pe/createurl ["e" "store" "search"])
                           :on-success [:save-appstore-installers]
                           :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :search-appstore
  (fn search-appstore-handler
    [{db :db} _]
    {:dispatch [:http-get {:url (str (pe/createurl ["e" "store" "search"]) "?general=" (get-in db [:store :filter]))
                           :on-success [:save-appstore-installers]
                           :on-failure [:dashboard-failure]}]}))
(rf/reg-event-fx
  :save-appstore-installers
  (fn save-appstore-installers-handler
    [{db :db} [_ result]]
    {:db (-> db
             (assoc-in [:store :installers] result))}))


(rf/reg-event-fx
  :download-installer
  (fn download-installer-handler
    [{db :db} [_ id]]
    (let [installer (get-in db [:store :installers (keyword id)])
          name (:name installer)
          version (last (sort (:versions installer)))]
    {:dispatch [:http-post {:url (pe/createurl ["e" "store" "download"])
                            :on-success [:download-installer-success]
                            :on-failure [:dashboard-failure]
                            :post-data {:id id :version version :name name}}]})))

(rf/reg-event-db
  :download-installer-success
  (fn download-installer-success-handler
    [db _]
    (assoc-in db [:dashboard :alert] {:type "success" :message "Installer  downloaded successfully"})))

)