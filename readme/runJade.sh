
jadeLoc=$HOME/MARA/jade-3.7/jade

java -classpath "$jadeLoc/lib/jade.jar:$jadeLoc/lib/jadeTools.jar:$jadeLoc/lib/http.jar:$jadeLoc/crimson.jar:$jadeLoc/classes:$jadeLoc:." jade.Boot -gui $@