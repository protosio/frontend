(ns ^:figwheel-always protosfrontend.core
    (:require [reagent.core :as reagent]
              [reagent.session :as session]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [secretary.core :as secretary :include-macros true]
              [ajax.core :refer [GET POST]])
    (:import goog.History))

(enable-console-print!)

(println "Executing frontend code")



;; define your app data so that it doesn't get over-written on reload

(defonce app-state (reagent/atom {:text "Hello worlds!" :apps nil}))

;; utils

(def truthy? #{"true"})

;;-------------------------
;; Backend comm.

(defn response-handler [response]
  (println "Received response from backend")
  (swap! app-state assoc :apps (get response "Apps"))
  (println "Saved apps ")) ;(get @app-state :apps)))

(defn update-app [response]
  (println @app-state)
  (println "Updating app: " (get response "Name"))
  (swap! app-state assoc-in [:apps (get response "Name")] response)
  (println @app-state))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn get-apps []
  (.log js/console (str "Retrieving applications"))
  (GET "/apps"
     {:handler response-handler
      :error-handler error-handler
      :format :json
      :response-format :json}))

(defn toggle-app [appkey app-status]
  (println "Changing state for application " appkey " from " app-status " to " (not (truthy? app-status)))
  (POST (str "/apps/" appkey)
    {:handler update-app
     :error-handler error-handler
     :format :json
     :response-format :json
     :params {"Status" {"Running" (not (truthy? app-status))}}}))

;;-------------------------
;; Page elements

(defn menu []
  [:nav {:class "navbar navbar-default navbar-static-top"}
   [:div {:class "container"}
    [:div {:class "navbar-header"}
     [:button {:type "button" :class "navbar-toggle collapsed" :data-toggle "collapse" :data-target "#navbar" :aria-expanded "false" :aria-controls "navbar"}
      [:span {:class "sr-only"} "Toggle navigation"]
      [:span {:class "icon-bar"}]
      [:span {:class "icon-bar"}]
      [:span {:class "icon-bar"}]]
     [:a {:class "navbar-brand" :href "#/"} "Protos"]]
    [:div {:id "navbar" :class "collapse navbar-collapse"}
     [:ul {:class "nav navbar-nav"}
      [:li [:a {:href "#/"} "Home"]]
      [:li [:a {:href "#/about"} "About page"]]]]
    ]])

(defn app-list []
  [:table {:class "table table-hover"}
    [:tr
      [:th "Name"]
      [:th "State"]
      [:th "Action"]]
    (for [[title body] (get @app-state :apps)]
      (let [app-status (str (get-in body ["Status" "Running"]))]
      [:tr {:key title :border "1" :style {:width "100%"}}
       [:td [:a {:href (str "#/apps/" title)} title]]
       [:td app-status]
       [:td [:button {:on-click (fn [e] (.preventDefault e)
                                       (toggle-app title app-status))} "Start/Stop"]]]))])

(defn regular-page [left right]
  [:div
    (menu)
    [:div {:class "container"}
     [:div {:class "row"}
      [:div {:class "col-md-1"}
       left]
      [:div {:class "col-md-11"}
       right]]]])

;;--------------------------
;; Pages

(defn home-page []
  [:div
  (regular-page
  [:button {:on-click (fn [e] (.preventDefault e)
                              (get-apps))} "Refresh"]
  [app-list])])

(defn about-page []
  [:div
  (menu)
  [:h1 "This is the about text"]])

(defn app-page [app]
  [:div
   (menu)
   [:h1 app]])


(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

(secretary/defroute "/apps/" [name]
                    (session/put! :current-page #'app-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))


(init!)