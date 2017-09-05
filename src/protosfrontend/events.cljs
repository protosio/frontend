(ns protosfrontend.events
    (:require
        [clojure.string :as string]
        [ajax.core :as ajax]
        [re-frame.core :as rf]
        [day8.re-frame.http-fx]
        [clairvoyant.core :refer-macros [trace-forms]]
        [re-frame-tracer.core :refer [tracer]]))


(trace-forms {:tracer (tracer :color "green")}

;; -- Static fata ----------------------

(def resources {:apps "apps"
                :installers "installers"
                :app ["apps" :param]
                :installer ["installers" :param]
                :installer-metadata ["installers" :param "metadata"]})

;; -- Helper functions ---------------------------------------------

(defn createurl [urlkeys]
    (str "/" (string/join "/" (flatten [urlkeys]))))

(defn geturl [[resource & params]]
    (let [urlp (resource resources)
          urlreplaced (for [x urlp]
                        (if (= x :param)
                          (first params)
                          x))
          url (str "/" (string/join "/" urlreplaced))]
     url))


;; -- Event Handlers -----------------------------------------------

(rf/reg-event-db
  :initialize
  (fn [_ [_ active-page]]
    {:apps {}
     :installers {}
     :active-page active-page
     :modal-data {:show-modal false}
     :form-data {}}))


(rf/reg-event-db ;; Saves the result of a HTTP request, to a top level DB key or a nested one.
  :process-response
  (fn process-response-handler
    [db [_ dbkeys result]]
    (assoc-in db dbkeys result)))

(rf/reg-event-db
  :bad-response
  (fn bad-response-handler
    [db [_ result]]
    (println result)
    db))

;; -- Form events -----------------------------------------------

(rf/reg-event-db
  :update-form-data
  (fn update-form-data-handler
    [db [_ keys value]]
    (assoc-in db (cons :form-data keys) value)))

(rf/reg-event-db
  :reset-form-data
  (fn reset-form-data-handler
    [db [_ _]]
    (assoc db :form-data {})))

;; -- Modal events -----------------------------------------------

(rf/reg-event-fx
  :open-modal
  (fn open-modal-handler
      [{db :db} [_ modal-type modal-params]]
      {:dispatch [:update-form-data [:imageid] modal-params]
       :db (assoc db :modal-data {:show-modal true :active-modal modal-type :modal-params modal-params})}))

(rf/reg-event-fx
  :close-modal
  (fn close-modal-handler
    [{db :db} [_ modal-type]]
    {:dispatch [:reset-form-data]
     :db (assoc db :modal-data {:show-modal false :active-modal modal-type :modal-params nil})}))

;; -- Activate page -----------------------------------------------

(rf/reg-event-fx
  :set-active-page
  (fn set-active-page-handler
    [{db :db} [_ active-page update-event]]
    (if update-event
     {:db (assoc db :active-page active-page)
      :dispatch update-event}
     {:db (assoc db :active-page active-page)})))

;; -- Resource operations -----------------------------------------

(rf/reg-event-fx
  :get-resources
  (fn get-resources
    [{db :db} [_ _]]
    {:dispatch [:http-get {:url (createurl ["resources"])
                           :result-db-key [:resources]}]
     :db db}))

(rf/reg-event-fx
  :get-installer
  (fn get-installer-handler
    [{db :db} [_ installer-id]]
    {:dispatch [:http-get {:url (createurl ["installers" installer-id])
                           :result-db-key [:installers (keyword installer-id)]}]
     :db db}))

(rf/reg-event-fx
  :create-installer-metadata
  (fn create-installer-metadata-handler
    [{db :db} [_ installer-id]]
    {:dispatch [:http-post {:url (createurl ["installers" installer-id "metadata"])
                            :response-format :raw
                            :on-success [:get-installer installer-id]}]
     :db db}))

(rf/reg-event-fx
  :get-installers
  (fn get-installers
    [{db :db} [_ _]]
    {:dispatch [:http-get {:url (createurl ["installers"])
                           :result-db-key [:installers]}]
     :db db}))

(rf/reg-event-fx
  :get-apps
  (fn get-apps
    [{db :db} [_ _]]
    {:dispatch [:http-get {:url (createurl ["apps"])
                           :result-db-key [:apps]}]
     :db db}))

(rf/reg-event-fx
  :get-app
  (fn get-app-handler
    [{db :db} [_ app-id]]
    {:dispatch [:http-get {:url (createurl ["apps" app-id])
                           :result-db-key [:apps (keyword app-id)]}]
     :db db}))

(rf/reg-event-fx
  :create-app
  (fn create-app-handler
    [{db :db} [_ _]]
    {:dispatch [:http-post {:url (createurl ["apps"])
                            :on-success [:set-active-page [:apps-page] [:get-apps]]}]
     :db db}))

(rf/reg-event-fx
  :remove-app
  (fn remove-app-handler
    [{db :db} [_ app-id]]
    {:dispatch [:http-delete {:url (createurl ["apps" app-id])
                              :response-format :raw
                              :on-success [:set-active-page [:apps-page] [:get-apps]]}]
     :db db}))

(rf/reg-event-fx
  :app-state
  (fn app-state-handler
    [{db :db} [_ app-id state]]
    {:dispatch [:http-post {:url (createurl ["apps" app-id "action"])
                            :response-format :raw
                            :on-success [:set-active-page [:app-page app-id] [:get-app app-id]]
                            :post-data {:name state}}]
     :db db}))

;; -- HTTP operations ---------------------------------------------

(rf/reg-event-fx
  :http-get
  (fn http-get-handler
    [{db :db} [_ params]]
   {:http-xhrio {:method          :get
                 :uri             (:url params)
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:process-response (:result-db-key params)]
                 :on-failure      [:bad-response]}
    :db  (assoc db :loading? true)}))

(rf/reg-event-fx
  :http-post
  (fn http-post-handler
    [{db :db} [_ params]]
    {:http-xhrio {:method          :post
                  :uri             (:url params)
                  :params          (let [post-data (:post-data params)]
                                    (if post-data
                                      post-data
                                      (:form-data db)))
                  :format          (ajax/json-request-format)
                  :response-format (if (= (:response-format params) :raw)
                                    (ajax/raw-response-format)
                                    (ajax/json-response-format {:keywords? true}))
                  :on-success      (:on-success params)
                  :on-failure      [:bad-response]}
     :dispatch [:close-modal]
     :db  (assoc db :loading? true)}))

(rf/reg-event-fx
  :http-delete
  (fn http-delete-handler
    [{db :db} [_ params]]
    {:http-xhrio {:method          :delete
                  :uri             (:url params)
                  :format          (ajax/url-request-format)
                  :response-format (ajax/raw-response-format)
                  :on-success      (:on-success params)
                  :on-failure      [:bad-response]}
     :dispatch [:close-modal]
     :db  (assoc db :loading? true)}))

)