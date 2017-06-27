(ns protosfrontend.core
  (:require [reagent.core :as reagent]
            [reagent.session :as session]
            [re-frame.core :as rf]
            [ajax.core :as ajax]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [secretary.core :as secretary :include-macros true]
            [protosfrontend.events :as e]
            [protosfrontend.subs :as s]
            [protosfrontend.views :as v])
  (:import goog.History))

(enable-console-print!)

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:update-and-set-active-page :apps [:apps-page]]))

(secretary/defroute "/apps/:id" {:as params}
  (let [id (:id params)]
   (rf/dispatch [:update-and-set-active-page [:apps id] [:app-page id]])))

(secretary/defroute "/installers" []
  (rf/dispatch [:update-and-set-active-page :installers [:installers-page]]))

(secretary/defroute "/installers/:id" {:as params}
  (let [id (:id params)]
   (rf/dispatch [:update-and-set-active-page [:installers id] [:installer-page id]])))

(secretary/defroute "/about" []
  (rf/dispatch [:set-active-page [:about-page]]))

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
  (rf/dispatch-sync [:initialize [:apps-page]])
  (reagent/render [v/current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))

(init!)
