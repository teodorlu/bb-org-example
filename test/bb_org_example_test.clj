(ns bb-org-example-test
  (:require [bb-org-example :as e]
            [clojure.test :refer [deftest is]]))

(deftest slug->org-path
  (is (= "posts/org-on-quickblog.org"
         (-> "org-on-quickblog" e/slug->org-path str))))
