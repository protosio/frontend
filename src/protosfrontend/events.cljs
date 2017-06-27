(ns protosfrontend.events
    (:require
        [ajax.core :as ajax]
        [re-frame.core :as rf]
        [day8.re-frame.http-fx]))

;; -- Event Handlers -----------------------------------------------

(rf/reg-event-db
  :initialize
  (fn [_ [_ active-page]]
    {:apps {}
     :installers {}
     :active-page active-page
     :urls {:apps "/apps"
            :installers "/installers"}
     :show-create-app-modal false}))

(rf/reg-event-db
  :process-response
  (fn [db [_ dbkey result]]
    (assoc-in db dbkey result)))

(rf/reg-event-db
  :bad-response
  (fn [db [_ result]]
    (println result)
    db))

(rf/reg-event-db
  :create-app-form
  (fn [db [_ keys value]]
    (assoc-in db (cons :create-app-form keys) value)))

(rf/reg-event-db
  :set-active-page
  (fn [db [_ active-page]]
    (assoc db :active-page active-page)))

(rf/reg-event-db
  :show-create-app-modal
  (fn [db [_ modal-open image-id]]
    (assoc db :show-create-app-modal modal-open :create-app-form {:imageid image-id})))

(rf/reg-event-db
  :show-installer-metadata-modal
  (fn [db [_ modal-open image-id]]
    (assoc db :show-installer-metadata-modal modal-open :nstaller-metadata-form {:imageid image-id})))

(rf/reg-event-fx
  :update-and-set-active-page
  (fn
    [{db :db} [_ dbkey active-page]]
    {:dispatch (if (vector? dbkey)
                (into [:update-resource] dbkey)
                [:update-list dbkey])
     :db (assoc db :active-page active-page)}))

(rf/reg-event-fx
  :update-list
  (fn
    [{db :db} [_ dbkey]]
   {:http-xhrio {:method          :get
                 :uri             (get-in db [:urls dbkey])
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:process-response [dbkey]]
                 :on-failure      [:bad-response]}
    :db  (assoc db :loading? true)}))

(rf/reg-event-fx
  :update-resource
  (fn
    [{db :db} [_ dbkey id]]
   {:http-xhrio {:method          :get
                 :uri             (str (get-in db [:urls dbkey]) "/" id)
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:process-response [dbkey (keyword id)]]
                 :on-failure      [:bad-response]}
    :db  (assoc db :loading? true)}))

(rf/reg-event-fx
  :remove-resource
  (fn
    [{db :db} [_ dbkey id redirect-page]]
    {:http-xhrio {:method          :delete
                  :uri             (str (get-in db [:urls dbkey]) "/" id)
                  :format          (ajax/url-request-format)
                  :response-format (ajax/raw-response-format)
                  :on-success      [:update-and-set-active-page dbkey redirect-page]
                  :on-failure      [:bad-response]}
     :db  (assoc db :loading? true)}))

(rf/reg-event-fx
  :create-resource
  (fn
    [{db :db} [_ dbkey form-data-key redirect-page]]

    {:http-xhrio {:method          :post
                  :uri             (get-in db [:urls dbkey])
                  :params          (form-data-key db)
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:update-and-set-active-page dbkey redirect-page]
                  :on-failure      [:bad-response]}
     :db  (assoc db :loading? true :show-create-app-modal false)}))

(rf/reg-event-fx
  :create-resource-action
  (fn
    [{db :db} [_ dbkey id action redirect-page]]
    {:http-xhrio {:method          :post
                  :uri             (str (get-in db [:urls dbkey]) "/" id "/action")
                  :params          {:name action}
                  :format          (ajax/json-request-format)
                  :response-format (ajax/raw-response-format)
                  :on-success      [:update-resource dbkey id]
                  :on-failure      [:bad-response]}
     :db  (assoc db :loading? true)}))