(ns protosfrontend.views
    (:require
        [protosfrontend.components.header :as header]
        [protosfrontend.components.footer :as footer]
        [protosfrontend.components.cards :as cards]
        [protosfrontend.dashboard.home :as home]
        [protosfrontend.dashboard.app :as app]
        [protosfrontend.dashboard.store :as store]
        [protosfrontend.dashboard.task :as task]
        [protosfrontend.dashboard.resource :as resource]
        [protosfrontend.init.views :as initviews]
        [protosfrontend.auth.views :as authviews]
        [re-frame.core :as rf]
        [free-form.re-frame :as free-form]
        [clairvoyant.core :refer-macros [trace-forms]]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "brown")}

;; ---------------------------------------
;; Pages

(defn dashboard-page [inner title active-page]
  [:div {:class "page"}
    [:div {:class "page-main"}
      [header/top active-page]
      [:div {:class "my-3 my-md-5"}
        [inner title]]]
    [footer/bar]])

(defn init-page []
  [:div {:class "page"}
    [:div {:class "page-single"}
      [initviews/init-wizard]]])

(defn login-page []
  [:div {:class "page"}
    [:div {:class "page-single"}
      [authviews/login-form]]])

(defn current-page []
  (let [[active-page _ item-id]  @(rf/subscribe [:active-page])]
    (condp = active-page
      :init-page            [init-page]
      :login-page           [login-page]
      :dashboard-page       [dashboard-page home/home-page "Dashboard" active-page]
      :task-page            [dashboard-page task/task-page item-id active-page]
      :tasks-page           [dashboard-page task/tasks-page "Tasks" active-page]
      :app-page             [dashboard-page app/app-page item-id active-page]
      :apps-page            [dashboard-page app/apps-page "Apps" active-page]
      :store-page           [dashboard-page store/store-page "Store" active-page]
      :store-installer-page [dashboard-page store/store-installer-page item-id active-page]
      :resources-page       [dashboard-page resource/resources-page "Resources" active-page]
      :resource-page        [dashboard-page resource/resource-page item-id active-page])))

)
