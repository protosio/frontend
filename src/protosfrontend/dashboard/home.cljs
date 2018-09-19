(ns dashboard.home
    (:require
      [re-frame.core :as rf]
      [components.cards :as cards]
      [protosfrontend.util :as util]))

(defn home-page [title]
  [:div {:class "page-main"}]
    [:div {:class "container"}
      (if title
        [:div.page-header [:h1.page-title title]])
      [:div {:class "row row-cards"}
        [:div {:class "col-6 col-sm-4 col-lg-2"} [cards/stats "Apps" 3]]
        [:div {:class "col-6 col-sm-4 col-lg-2"} [cards/stats "Resources" 7]]
        [:div {:class "col-6 col-sm-4 col-lg-2"} [cards/stats "Providers" 2]]]])