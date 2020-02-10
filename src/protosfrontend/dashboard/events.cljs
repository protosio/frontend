(ns protosfrontend.dashboard.events
    (:require
        [re-frame.core :as rf]
        [linked.core :as linked]
        [day8.re-frame.http-fx]
        [protosfrontend.util :as util]
        [com.degel.re-frame.storage]
        [district0x.re-frame.interval-fx]))

;; -- Common operations ----------------------------------------

(rf/reg-event-fx
  :dashboard-failure
  (fn dashboard-failure-handler
    [{db :db} [_ result]]
    {:db (assoc-in db [:dashboard :alert] {:type "danger" :message (let [err (get-in result [:response :error])
                                                                         err-status (:status-text result)
                                                                         parse-status (get-in result [:parse-error :status-text])]
                                                                         (if err
                                                                          err
                                                                          (str err-status "(" parse-status ")")))})}))

(rf/reg-event-db
  :save-response
  (fn save-response-handler
    [db [_ path result]]
    (assoc-in db path result)))

;; -- Tasks ----------------------------------------------------

(rf/reg-event-fx
  :get-tasks
  (fn get-tasks-handler
    [_ _]
    {:dispatch [:http-get {:url (util/createurl ["e" "tasks"])
                           :on-success [:save-tasks]
                           :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :save-tasks
  (fn save-tasks-handler
    [{db :db} [_ result]]
    (let [tasks (util/fmap util/replace-time-in-task result)
          sorted-tasks (into (linked/map) (util/sort-tasks tasks))]
          {:db (assoc db :tasks sorted-tasks)})))

(rf/reg-event-fx
  :get-task
  (fn get-task-handler
    [_ [_ task-id]]
    {:dispatch [:http-get {:url (util/createurl ["e" "tasks" task-id])
                           :on-success [:save-task (keyword task-id)]
                           :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :save-task
  (fn save-task-handler
    [{db :db} [_ task-id result]]
    (let [task (util/replace-time-in-task result)]
         {:db (assoc-in db [:tasks task-id] task)})))

(rf/reg-event-fx
  :cancel-task
  (fn cancel-task-handler
    [_ [_ task-id]]
    {:dispatch [:http-put {:url (util/createurl ["e" "tasks" task-id "cancel"])
                           :on-failure [:dashboard-failure]}]}))

;; -- Installers -----------------------------------------------

(rf/reg-event-fx
  :get-installer
  (fn get-installer-handler
    [_ [_ installer-id]]
    {:dispatch [:http-get {:url (util/createurl ["e" "installers" installer-id])
                           :on-success [:save-response [:installers (keyword installer-id)]]
                           :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :create-installer-metadata
  (fn create-installer-metadata-handler
    [_ [_ installer-id]]
    {:dispatch [:http-post {:url (util/createurl ["e" "installers" installer-id "metadata"])
                            :response-format :raw
                            :on-success [:get-installer installer-id]}]}))

(rf/reg-event-fx
  :get-installers
  (fn get-installers-handler
    [_ _]
    {:dispatch [:http-get {:url (util/createurl ["e" "installers"])
                           :on-success [:save-response [:installers]]
                           :on-failure [:dashboard-failure]}]}))

;; -- Apps -----------------------------------------------------

(rf/reg-event-fx
  :get-apps
  (fn get-apps-handler
    [_ _]
    {:dispatch [:http-get {:url (util/createurl ["e" "apps"])
                           :on-success [:save-response [:apps]]
                           :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :get-app
  (fn get-app-handler
    [_ [_ app-id]]
    {:dispatch-n [[:http-get {:url (util/createurl ["e" "apps" app-id])
                              :on-success [:save-app (keyword app-id)]
                              :on-failure [:dashboard-failure]}]]}))

(rf/reg-event-fx
  :save-app
  (fn save-app-handler
    [{db :db} [_ app-id result]]
    {:db (assoc-in db [:apps app-id] result)}))

(rf/reg-event-fx
  :create-app
  (fn create-app-handler
    [{db :db} [_ installer-id selected-version]]
    {:dispatch [:http-post {:url (util/createurl ["e" "apps"])
                            :on-success [:create-app-success]
                            :on-failure [:dashboard-failure]
                            :post-data {:installer-id installer-id :installer-version selected-version :name (get-in db [:create-app :form :name]) :installer-params (get-in db [:create-app :form :installer-params])}}]}))

(rf/reg-event-fx
  :create-app-success
  (fn create-app-success-handler
    [{db :db} [_ result]]
    {:redirect-to [:apps-page]
     :db (-> db
             (update-in [:create-app] dissoc :form))
     :dispatch [:save-task (keyword (:id result)) result]}))

(rf/reg-event-fx
  :remove-app
  (fn remove-app-handler
    [_ [_ app-id]]
    {:dispatch [:http-delete {:url (util/createurl ["e" "apps" app-id])
                              :on-success [:remove-app-success (keyword app-id)]
                              :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :remove-app-success
  (fn remove-app-success-handler
    [_ [_ _]]
    {:redirect-to [:apps-page]}))

(rf/reg-event-fx
  :app-state
  (fn app-state-handler
    [_ [_ app-id state]]
    {:dispatch [:http-post {:url (util/createurl ["e" "apps" app-id "action"])
                            :on-success [:app-state-success (keyword app-id)]
                            :on-failure [:dashboard-failure]
                            :post-data {:name state}}]}))

(rf/reg-event-fx
  :app-state-success
  (fn app-state-success-handler
    [_ [_ _ task]]
    (let [task-id (keyword (:id task))]
         {:dispatch [:save-task task-id task]})))

;; -- Resources ------------------------------------------------

(rf/reg-event-fx
  :get-resources
  (fn get-resources
    [_ _]
    {:dispatch [:http-get {:url (util/createurl ["e" "resources"])
                           :on-success [:save-response [:resources]]
                           :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :get-resource
  (fn get-resource-handler
    [_ [_ rsc-id]]
    {:dispatch [:http-get {:url (util/createurl ["e" "resources" rsc-id])
                           :on-success [:save-response [:resources (keyword rsc-id)]]
                           :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :remove-resource
  (fn remove-resource-handler
    [_ [_ id]]
    {:dispatch [:http-delete {:url (util/createurl ["e" "resources" id])
                              :on-success [:remove-resource-success]
                              :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :remove-resource-success
  (fn remove-resource-success-handler
    [_ _]
    {:redirect-to [:resources-page]}))

;; -- App store ------------------------------------------------

(rf/reg-event-fx
  :get-appstore-all
  (fn get-appstore-all-handler
    [_ _]
    {:dispatch [:http-get {:url (util/createurl ["e" "store" "search"])
                           :on-success [:save-appstore-installers]
                           :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :search-appstore
  (fn search-appstore-handler
    [{db :db} _]
    {:dispatch [:http-get {:url (str (util/createurl ["e" "store" "search"]) "?general=" (get-in db [:store :filter]))
                           :on-success [:save-appstore-installers]
                           :on-failure [:dashboard-failure]}]}))
(rf/reg-event-fx
  :save-appstore-installers
  (fn save-appstore-installers-handler
    [{db :db} [_ result]]
    {:db (-> db
             (assoc-in [:store :installers] result))}))

;; -- Dashboard ------------------------------------------------

(rf/reg-event-fx
  :get-dashboard
  (fn get-dashboard-handler
    [{db :db} _]
    {:dispatch-n [[:get-services] [:get-hwstats] (when (not (:instance-info db)) [:get-instance-info])]}))


(rf/reg-event-fx
  :get-instance-info
  (fn get-instance-info-handler
    [_ _]
    {:dispatch [:http-get {:url (util/createurl ["e" "info"])
                           :on-success [:save-response [:instance-info]]
                           :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :get-services
  (fn get-services-handler
    [_ _]
    {:dispatch [:http-get {:url (util/createurl ["e" "services"])
                           :on-success [:save-response [:services]]
                           :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
  :get-hwstats
  (fn get-hwstats-handler
    [_ _]
    {:dispatch [:http-get {:url (util/createurl ["e" "hwstats"])
                           :on-success [:save-response [:hwstats]]
                           :on-failure [:dashboard-failure]}]}))

(rf/reg-event-fx
 :get-all
 (fn get-all-handler
   [_ _]
   {:dispatch-n [[:get-apps]
                 [:get-tasks]
                 [:get-resources]]}))
