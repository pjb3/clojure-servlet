(in-ns 'clojure.servlet.ClojureServlet)
(clojure/refer 'clojure)
(.loadResourceScript clojure.lang.RT "genclass.clj")
(clojure/gen-and-save-class (str (.getProperty System "user.dir") "/build/") 'clojure.servlet.ClojureServlet :extends javax.servlet.http.HttpServlet) 