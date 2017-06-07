(ns protosfrontend.core
  (:require [reagent.core :as reagent]
            [reagent.session :as session]
            [re-frame.core :as rf]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [secretary.core :as secretary :include-macros true])
  (:import goog.History))

(enable-console-print!)

;; -- Event Handlers -----------------------------------------------

(rf/reg-event-db
  :initialize
  (fn [_ _]
    {:apps {}
     :installers {}}))

(rf/reg-event-db
  :process-response
  (fn [db [_ dbkey result]]
    (assoc db dbkey result)))

(rf/reg-event-fx
  :update-list
  (fn
    [{db :db} [_ uri dbkey]]
   
    {:http-xhrio {:method          :get
                  :uri             uri
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:process-response dbkey]
                  :on-failure      [:bad-response]}
     :db  (assoc db :loading? true)}))

;; -- Queries -----------------------------------------------

(rf/reg-sub
  :apps
  (fn [db _]
    (-> db
        :apps)))

(rf/reg-sub
  :installers
  (fn [db _]
    (-> db
        :installers)))

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
      [:li [:a {:href "#/installers"} "Installers"]]
      [:li [:a {:href "#/about"} "About page"]]]]]])

(defn installer-list []
  [:table {:class "table table-hover"}
   [:tbody
    [:tr
      [:th "Name"]
      [:th "ID"]]
    (let [installers @(rf/subscribe [:installers])]
      (for [{Name :Name, ID :ID} installers]
        [:tr {:key ID :style {:width "100%"}}
          [:td [:a {:href (str "/#/installers/" ID)} Name]]
          [:td ID]]))]])

(defn app-list []
  [:table {:class "table table-hover"}
   [:tbody
    [:tr
      [:th "Name"]
      [:th "ID"]
      [:th "Status"]]
    (let [apps @(rf/subscribe [:apps])]
      (for [{name :name, id :id, status :status} apps]
        [:tr {:key id :style {:width "100%"}}
          [:td [:a {:href (str "/#/apps/" id)} name]]
          [:td id]
          [:td status]]))]])


(defn regular-page [left right]
  [:div
    (menu)
    [:div {:class "container"}
     [:div {:class "row"}
      [:div {:class "col-md-1"}
       left]
      [:div {:class "col-md-11"}
       right]]]])

;; -------------------------
;; Pages
(defn home-page
  []
  [:div
   (regular-page
    [:button {:on-click #(rf/dispatch [:update-list "/apps" :apps])} "Refresh"]
    [app-list])])

(defn installers-page
  []
  [:div
   (regular-page
    [:button {:on-click #(rf/dispatch [:update-list "/installers" :installers])} "Refresh"]
    [installer-list])])

(defn about-page []
  [:div
   (menu)
   [:div {:class "container"}
    [:h1 "This is the about text"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:update-list "/apps" :apps])
  (session/put! :current-page home-page))

(secretary/defroute "/installers" []
  (rf/dispatch [:update-list "/installers" :installers])
  (session/put! :current-page installers-page))

(secretary/defroute "/about" []
  (session/put! :current-page about-page))

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
  (rf/dispatch-sync [:initialize])
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))

(init!)
