(ns bb-org-example
  (:require [babashka.fs :as fs]
            [bb-org-example.pandoc :as pandoc]
            [hiccup2.core :as hiccup]))

(defn list-post-slugs []
  (->> (fs/list-dir "posts")
       (filter (comp #{"org"} fs/extension))
       (map fs/file-name)
       (map fs/strip-ext)))

(def posts-root "posts")

(defn slug->org-path [slug]
  (fs/file posts-root (str slug ".org")))

(def site-root "site")

(defn slug->html-path [slug]
  (fs/file site-root (str slug ".html")))

(defn build-post [slug]
  (let [org-path (slug->org-path slug)
        html-path (slug->html-path slug)]
    (spit html-path (-> org-path slurp pandoc/from-org pandoc/to-html-standalone))
    [:created (str html-path) :from (str org-path)]))

(defn slug->link [slug]
  (str slug ".html"))

(defn index-hiccup [slugs]
  [:html
   [:body
    [:h1 "Your site"]
    [:ul
     (for [slug slugs]
       [:li [:a {:href (slug->link slug)} slug]])]]])

(defn build-index [slugs]
  (let [html-path (fs/file site-root "index.html")]
    (spit html-path
          (str "<!DOCTYPE html>\n"
               (hiccup/html (index-hiccup slugs))))
    [:created (str html-path) :from (map (comp str slug->org-path) slugs)]))

(defn build []
  (concat (->> (list-post-slugs)
               (map (fn [slug]
                      (build-post slug)))
               doall)
          [(build-index (list-post-slugs))]))

(defn clean-site-html []
  (->> (fs/glob site-root "*.html")
       (map (fn [f]
              (fs/delete f)
              [:deleted (str f)]))
       doall))

(defn clean []
  (clean-site-html))

(comment
  (clean)
  (build)
  ((requiring-resolve 'clojure.repl.deps/sync-deps))

  )
