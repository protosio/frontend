(ns protosfrontend.routes
    (:require [re-frame.core :as rf]
              [bidi.bidi :as bidi]
              [pushy.core :as pushy]))


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

(defn- dispatch-route [matched-route]
  (let [page-handler (:handler matched-route)
        route-params (:route-params matched-route)
        event        [:set-active-page page-handler]]
    (rf/dispatch (if route-params
                     (vec (concat event (vec (vals route-params))))
                     event))))

(defn app-routes []
  (pushy/start! (pushy/pushy dispatch-route parse-url)))

(def url-for (partial bidi/path-for routes))