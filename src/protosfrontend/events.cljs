(ns protosfrontend.events
    (:require
      [clojure.string :as string]
      [ajax.core :as ajax]
      [re-frame.core :as rf]
      [linked.core :as linked]
      [day8.re-frame.http-fx]
      [protosfrontend.util :as util]
      [com.smxemail.re-frame-cookie-fx]
      [com.degel.re-frame.storage]
      [district0x.re-frame.interval-fx]
      [clairvoyant.core :refer-macros [trace-forms]]
      [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "green")}

;; -- Event Handlers -----------------------------------------------

(rf/reg-event-fx
  :initialize
  [(rf/inject-cofx :cookie/get [:token])]
  (fn initialize
    [{db :db cookies :cookie/get} _]
    {:dispatch-n [[:load-userinfo]]
     :init-ws (:token cookies)
     :db (if (:initialized db)
             db
             {:apps {}
              :tasks (linked/map)
              :store {}
              :active-page [:dashboard-page]
              :form-data {}
              :init-wizard {:step 1}
              :alert nil
              :ws-connected false
              :initialized true
              :auth {}
              :loading? false})}))

(rf/reg-event-fx
  :noop
  (fn noop-handler
    [_ _]
    {}))

;; -- Timer events --------------------------------

(rf/reg-event-fx
  :start-timer
  (fn start-timer-handler
    [_ [_ id freq event]]
    {:dispatch-interval {:id        id
                         :ms        freq
                         :dispatch  event}}))

(rf/reg-event-fx
  :stop-timer
  (fn stop-timer-handler
    [_ [_ id]]
    {:clear-interval {:id id}}))

;; -- Response processing events --------------------------------

(rf/reg-event-fx ;; Saves the result of a HTTP request, to a top level DB key or a nested one.
  :good-response
  (fn good-response-handler
    [{db :db} [_ options result]]
    {:dispatch-n [[:success-alert (let [msg (:alert-message options)] (if msg msg result)) (let [alert-key (:alert-key options)] (if alert-key alert-key :main))]
                  (when (:event options) [(:event options) result])]
     :dispatch-debounce {:id :disable-loading
                         :timeout 300
                         :dispatch [:disable-loading]}
     :db (if (:db-key options)
             (assoc-in db (:db-key options) result)
             db)}))

(rf/reg-event-fx
  :bad-response
  (fn bad-response-handler
    [{db :db} [_ options result]]
    {:dispatch [:fail-alert (get-in result [:response :error]) (let [alert-key (:alert-key options)] (if alert-key alert-key :main))]
     :dispatch-debounce {:id :disable-loading
                         :timeout 300
                         :dispatch [:disable-loading]}}))

(rf/reg-event-fx
  :request-finished
  (fn request-finished-handler
    [{db :db} [_ event result]]
    {:dispatch (condp = (:status result)
                      401 [:redirect-login]
                      424 [:redirect-init]
                      (conj event result))
     ;; FIX: for some reason the dispatch debounce doesnt propagate when doing a redirect, so setting the loading indicator to false is required here
     :db (if (or (= (:status result) 401) (= (:status result) 424))
             (assoc db :loading? false)
             db)
     :dispatch-debounce {:id :disable-loading
                         :timeout 300
                         :dispatch [:disable-loading]}}))

(rf/reg-event-fx
  :redirect-login
  (fn redirect-login-handler
    [{db :db} _]
    {:redirect-to [:login-page]
     :db (assoc db :previous-page (:active-page db))}))

(rf/reg-event-fx
  :redirect-init
  (fn redirect-init-handler
    [_ _]
    {:redirect-to [:init-page]}))

(rf/reg-event-db
  :disable-loading
  (fn disable-loading-handler
    [db _]
    (assoc db :loading? false)))

;; -- Alert events -----------------------------------------------

(rf/reg-event-db
  :success-alert
  (fn success-alert-handler
    [db [_ text key]]
    (assoc-in db [:alert key] {:type "success" :message text})))

(rf/reg-event-db
  :fail-alert
  (fn fail-alert-handler
    [db [_ text key]]
    (assoc-in db [:alert key] {:type "danger" :message text})))

;; -- Form events -----------------------------------------------

(rf/reg-event-db
  :set-form-value
  (fn set-form-value-handler
    [db [_ path value]]
    (assoc-in db path value)))

(rf/reg-event-db
  :update-form-data
  (fn update-form-data-handler
    [db [_ path value]]
    (assoc-in db path value)))

(rf/reg-event-db
  :reset-form-data
  (fn reset-form-data-handler
    [db [_ _]]
    (assoc db :form-data {})))

;; -- HTTP operations ---------------------------------------------

(rf/reg-event-fx
  :http-get
  [(rf/inject-cofx :cookie/get [:token])]
  (fn http-get-handler
    [{db :db cookies :cookie/get} [_ params]]
   {:http-xhrio {:method          :get
                 :uri             (:url params)
                 :headers         [:Authorization (clojure.string/join " " ["Bearer" (:token cookies)])]
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:request-finished (:on-success params)]
                 :on-failure [:request-finished (:on-failure params)]}
    :db  (assoc db :loading? true)}))

(rf/reg-event-fx
  :http-post
  [(rf/inject-cofx :cookie/get [:token])]
  (fn http-post-handler
    [{db :db cookies :cookie/get} [_ params]]
    {:http-xhrio {:method          :post
                  :uri             (:url params)
                  :headers         [:Authorization (clojure.string/join " " ["Bearer" (:token cookies)])]
                  :params          (let [post-data (:post-data params)]
                                    (if post-data
                                      post-data
                                      (:form-data db)))
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [:request-finished (:on-success params)]
                  :on-failure [:request-finished (:on-failure params)]}
     :db  (assoc db :loading? true)}))

(rf/reg-event-fx
  :http-put
  [(rf/inject-cofx :cookie/get [:token])]
  (fn http-put-handler
    [{db :db cookies :cookie/get} [_ params]]
    {:http-xhrio {:method          :put
                  :uri             (:url params)
                  :headers         [:Authorization (clojure.string/join " " ["Bearer" (:token cookies)])]
                  :params          (:put-data params)
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [:request-finished (:on-success params)]
                  :on-failure [:request-finished (:on-failure params)]}
     :db  (assoc db :loading? true)}))

(rf/reg-event-fx
  :http-delete
  [(rf/inject-cofx :cookie/get [:token])]
  (fn http-delete-handler
    [{db :db cookies :cookie/get} [_ params]]
    {:http-xhrio {:method          :delete
                  :uri             (:url params)
                  :headers         [:Authorization (clojure.string/join " " ["Bearer" (:token cookies)])]
                  :format          (ajax/url-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [:request-finished (:on-success params)]
                  :on-failure [:request-finished (:on-failure params)]}
     :db  (assoc db :loading? true)}))

)
