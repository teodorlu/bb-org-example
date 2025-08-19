(ns bb-org-example-test
  (:require [bb-org-example :as e]
            [clojure.string :as str]
            [clojure.test :refer [deftest is]]))

(deftest slug->org-path
  (is (= "posts/org-on-quickblog.org"
         (-> "org-on-quickblog" e/slug->org-path str))))

(deftest org->html
  (is (= "<p>hi <strong>there</strong></p>\n"
         (e/org->html "hi *there*"))))

(deftest org->html-standalone
  (is (str/starts-with?
       (e/org->html-standalone "hi *there*")
       "<!DOCTYPE html>")))
