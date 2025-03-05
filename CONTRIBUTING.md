# Règles de contributions
## 1. Les commits
- Les commits doivent être clairs et concis.
- Ils doivent commencer par un [gitmoji](https://gitmoji.dev/)

## 2. Les branches
Nous avons choisi la stratégie de branches GitFlow pour ce projet.

### La branche main
Elle est protégée et ne peut pas être push directement. Pour ajouter du code sur la branche main, il faut passer par une Pull Request.
Elle représente la version de production du projet.

### La branche dev
La branche dev est la branche de développement. C'est sur cette branche que l'on va ajouter les nouvelles fonctionnalités et s'assurer que tout fonctionne correctement avant de merger sur la branche main.

### Les branches feature
**feat/XX-nom-de-la-fonctionnalité** où XX est le numéro de l'issue sur github.

Les branches feature sont des branches de fonctionnalités. Elles sont créées à partir de la branche dev et sont supprimées une fois la fonctionnalité et mergée sur la branche dev.

## 3. Les Pull Requests
- Les Pull Requests doivent être nommées de la manière suivante : **🔀 Nom de la fonctionnalité**
