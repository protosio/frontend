(ns protosfrontend.routes
    (:require [re-frame.core :as rf]
              [bidi.bidi :as bidi]
              [pushy.core :as pushy]
              [clairvoyant.core :refer-macros [trace-forms]]
              [re-frame-tracer.core :refer [tracer]]))

(def static-data {:pages {:tasks-page [:get-tasks]
                          :task-page [:get-task]
                          :app-page [:get-app]
                          :apps-page [:get-apps]
                          :store-page [:get-appstore-all]
                          :resources-page [:get-resources]
                          :resource-page [:get-resource]}})

(def routes ["/ui/" {[""]               :dashboard-page
                     ["apps"]           :apps-page
                     ["apps/" :id]      :app-page
                     ["store"]          :store-page
                     ["store/" :id]     :store-installer-page
                     ["tasks"]          :tasks-page
                     ["tasks/" :id]     :task-page
                     ["resources"]      :resources-page
                     ["resources/" :id] :resource-page
                     ["init"]           :init-page
                     ["login"]          :login-page}])

(defn- parse-url [url]
  (bidi/match-route routes url))

(def url-for (partial bidi/path-for routes))

(defn- dispatch-route [matched-route]
  (let [page-handler (:handler matched-route)
        route-params (:route-params matched-route)
        event        [:set-active-page page-handler]]
    (rf/dispatch (if route-params
                     (vec (concat event (vec (vals route-params))))
                     event))))

(defn app-routes []
  (pushy/start! (pushy/pushy dispatch-route parse-url)))

(def history (atom nil))

(defn start! []
  (when (nil? @history)
    (reset! history (pushy/pushy dispatch-route parse-url)))
  (pushy/start! @history))

(defn redirect-to [& args]
  (when @history
    (let [path (apply url-for args)
          self-redirect (= path (pushy/get-token @history))]
      (pushy/set-token! @history path)
      (when self-redirect
        (when-let [parsed-path (parse-url path)]
          (dispatch-route parsed-path))))))

;; Event for triggering a re-direct
(rf/reg-fx
  :redirect-to
  (fn [path]
    (apply redirect-to path)))

(trace-forms {:tracer (tracer :color "green")}

(rf/reg-event-fx
  :navigate-to
  (fn navigate-to-handler
    [_ [_ path]]
    {:redirect-to path}))

;; -- Activate page -----------------------------------------------

(rf/reg-event-fx
  :set-active-page
  (fn set-active-page-handler
    [{db :db} [_ active-page item-id]]
    (let [ap (if item-id [active-page :id item-id] [active-page])
          update-event (get-in static-data [:pages active-page])
          res {:db (-> db
                       (assoc :active-page ap)
                       (assoc :loading? false)
                       (assoc-in [:dashboard :alert] nil))
               :dispatch-debounce {:action :cancel-all}}]
      (if update-event
        (assoc res :dispatch (if item-id
                                 (conj update-event item-id)
                                 update-event))
        res))))

)