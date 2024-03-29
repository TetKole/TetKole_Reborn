# Tetkole

## Sommaire

- [Tetkole](#tetkole)
  - [Sommaire](#sommaire)
  - [Dépendances](#dépendances)
    - [Windows](#windows)
    - [Linux](#linux)
      - [Ubuntu / Debian](#ubuntu--debian)
      - [Arch](#arch)
      - [Autres](#autres)
  - [Installation](#installation)
  - [Lancement](#lancement)
    - [Windows](#windows-1)
    - [Linux](#linux-1)
  - [Serveur](#serveur)
    - [Equipe des développeurs](#equipe-des-développeurs)


## Dépendances
   Avant de pouvoir lancer Tètkole, vous devez installer une dépendance : Java.
### Windows
   Pour installer Java, allez sur : https://www.java.com
   Et installer la dernière version de Java.
### Linux
   Cela dépendra de votre distribution, mais voici les plus connues :

#### Ubuntu / Debian

```shell
   sudo apt install openjdk-17-jre
```

#### Arch

```shell
   sudo pacman -S jre17-openjdk
```

#### Autres

Regardez sur https://www.java.com pour voir les différents formats d'installation.

## Installation

Récupérez dans les [releases de Tètkole](https://github.com/TetKole/TetKole_Reborn/releases) la version vous convenant (Linux ou Windows)

## Lancement

### Windows

Lancez l'installateur, et à la fin de l'installation, lancez l'application.

### Linux

Récupérez l'archive et récupérez son contenu.
Il vous suffit d'éxécuter le fichier `Tetkole` contenu dans **bin**

## Serveur

Pour configurer l'url du serveur, vous devez modifier la variable "servURL" contenu dans src/main/java/com.tetkole.tetkole.Main.java

### Equipe des développeurs

- Rémi CHAVANCE
- Antoine DEPOISIER
- Volodia FERNANDEZ
- Mathis TANGHE
- Irilind SALIHI
