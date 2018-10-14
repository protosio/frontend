(ns protosfrontend.views
    (:require
        [components.header :as header]
        [components.cards :as cards]
        [dashboard.home :as home]
        [dashboard.app :as app]
        [dashboard.store :as store]
        [dashboard.task :as task]
        [dashboard.resource :as resource]
        [init.views :as initviews]
        [auth.views :as authviews]
        [re-frame.core :as rf]
        [free-form.re-frame :as free-form]
        [clairvoyant.core :refer-macros [trace-forms]]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "brown")}

;; ---------------------------------------
;; Pages

(defn dashboard-page [inner title active-page]
  [:div {:class "page-main"}
    [header/top active-page]
    [:div {:class "my-3 my-md-5"}
      [inner title]]])

(defn init-page []
  [:div {:class "page-single"}
    [initviews/init-wizard]])

(defn login-page []
  [:div {:class "page-single"}
    [authviews/login-form]])

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
