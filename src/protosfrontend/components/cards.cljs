(ns components.cards
    (:require
      [re-frame.core :as rf]))

(defn stats [title nr]
  [:div {:class "card"}
    [:div {:class "card-body p-3 text-center"}
      [:div {:class "h1 m-0"} nr]
        [:div {:class "text-muted mb-4"} title]]])