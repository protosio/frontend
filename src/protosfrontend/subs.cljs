(ns protosfrontend.subs
    (:require
        [re-frame.core :as rf]))


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

(rf/reg-sub
  :show-create-app-modal
  (fn [db _]
    (-> db
        :show-create-app-modal)))

(rf/reg-sub
  :show-installer-metadata-modal
  (fn [db _]
    (-> db
        :show-installer-metadata-modal)))

(rf/reg-sub
  :create-app-form
  (fn [db _]
    (-> db
        :create-app-form)))

(rf/reg-sub
  :add-metadata-form
  (fn [db _]
    (-> db
        :add-metadata-form)))