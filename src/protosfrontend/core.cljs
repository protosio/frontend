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
  (fn [_ [_ active-page]]
    {:apps {}
     :installers {}
     :active-page active-page
     :urls {:apps "/apps"
            :installers "/installers"}}))

(rf/reg-event-db
  :process-response
  (fn [db [_ dbkey result]]
    (assoc-in db dbkey result)))

(rf/reg-event-db
  :bad-response
  (fn [db [_ result]]
    (println result)))

(rf/reg-event-db
  :set-active-page
  (fn [db [_ active-page]]
    (assoc db :active-page active-page)))

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

(rf/reg-sub
  :active-page
  (fn [db _]
    (-> db
        :active-page)))

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
      (for [{Name :Name, ID :ID} (vals installers)]
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
    [:button {:on-click #(rf/dispatch [:update-list :apps])} "Refresh"]
    [app-list])])

(defn installers-page
  []
  [:div
   (regular-page
    [:button {:on-click #(rf/dispatch [:update-list :installers])} "Refresh"]
    [installer-list])])

(defn installer-page
  [id]
  [:div
    (menu)
    [:div {:class "container"}
     (let [installers @(rf/subscribe [:installers])
           installer (get installers (keyword id))]
       [:div
        [:h1 (:Name installer)]
        [:div.installer-details "Image ID: " (:ID installer)]
        [:button {:class "label label-danger"
                  :on-click #(rf/dispatch [:remove-resource :installers (:ID installer) installers-page])} "Remove"]])]])

(defn about-page []
  [:div
   (menu)
   [:div {:class "container"}
    [:h1 "This is the about text"]]])

(defn current-page []
  [:div [@(rf/subscribe [:active-page])]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:update-and-set-active-page :apps home-page]))

(secretary/defroute "/installers" []
  (rf/dispatch [:update-and-set-active-page :installers installers-page]))

(secretary/defroute "/installers/:id" {:as params}
  (let [id (:id params)]
   (rf/dispatch [:update-and-set-active-page [:installers id] #(installer-page id)])))

(secretary/defroute "/about" []
  (rf/dispatch [:set-active-page about-page]))

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
  (rf/dispatch-sync [:initialize home-page])
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))

(init!)
