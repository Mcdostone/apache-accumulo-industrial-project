# Projet industriel

The dossier contient l'ensemble du code java développé au cours de ce projet industriel.


## Prérequis
 - maven
 - oracle JDK 1.8


## Structure du projet

Le projet est décomposé en 3 packages différents:
 - **project.industrial.features**: Classes permettant de valider le CDC des fonctionnalités
 - **project.industrial.examples**: Classes extraites du dépot github accumulo (pour avoir une codebase)
 - **project.industrial.benchmark**: Classes utilisés pour le CDC quantitatif


## Packager l'application

Pour construire le `jar`, exécutez la commande suivante:

``` bash
mvn package     ## Construit le jar sans include les dépendances
mvn -Pprod clean compile assembly:single    ## inclut les dépendances
```

## Auteurs
 - Samia BENJIDA
 - Louis-Pol KELNER
 - Pierre MAECKEREEL
 - Yann PRONO 


