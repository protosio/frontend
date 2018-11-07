(defproject protosfrontend "0.1.0-SNAPSHOT"
  :description "Frontend for Protos"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [org.clojure/core.async "0.4.474"]
                 [reagent "0.8.1"]
                 [reagent-utils "0.3.1"]
                 [reagent-forms "0.5.43"]
                 [cljsjs/react-bootstrap "0.31.5-0"]
                 [baking-soda "0.2.0"]
                 [cljsjs/react-transition-group "2.3.1-0"]
                 [cljsjs/react-popper "0.10.4-0"]
                 [bidi "2.1.4"]
                 [kibu/pushy "0.3.8"]
                 [cljs-ajax "0.7.3"]
                 [figwheel-sidecar "0.5.16"]
                 [com.cemerick/piggieback "0.2.1"]
                 [leiningen-core "2.8.1"]
                 [re-frame "0.10.5"]
                 [day8.re-frame/http-fx "0.1.6"]
                 [com.smxemail/re-frame-cookie-fx "0.0.2"]
                 [com.degel/re-frame-storage-fx "0.1.1"]
                 [district0x/re-frame-interval-fx "1.0.2"]
                 [com.pupeno/free-form "0.5.0"]
                 [binaryage/devtools "0.9.10"]
                 [org.clojars.stumitchell/clairvoyant "0.2.1"]
                 [day8/re-frame-tracer "0.1.1-SNAPSHOT"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [jarohen/chord "0.8.1"]
                 [com.cemerick/url "0.1.1"]]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.16"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:preloads [devtools.preload]
                                   :main protosfrontend.core
                                   :asset-path "/static/js/compiled/out"
                                   :output-to "resources/public/js/compiled/protosfrontend.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :closure-defines {"clairvoyant.core.devmode" true}
                                   :source-map-timestamp true}}
                       {:id "min"
                        :source-paths ["src/protosfrontend"]
                        :compiler {:output-to "resources/public/js/compiled/protosfrontend.js"
                                   :main protosfrontend.core
                                   :optimizations :advanced
                                   :pretty-print false}}]}

  :figwheel {
             :css-dirs ["resources/public/css"] ;; watch and update CSS
             ;; Start an nREPL server into the running figwheel process
             :nrepl-port 7888})