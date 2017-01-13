![Travis CI](https://api.travis-ci.org/melphi/algobox.svg?branch=master)
![Codeclimate](https://codeclimate.com/github/melphi/algobox/badges/gpa.svg)

# algobox

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8e7d9d9ed1b84e6693ab1fc1f4329eb4)](https://www.codacy.com/app/melphi/algobox?utm_source=github.com&utm_medium=referral&utm_content=melphi/algobox&utm_campaign=badger)

Algobox is an open source algorithmic trading software. The software is used in production however the profitable strategies are in a privare repository.

If you have a profitable trading strategy you would like to automate please write me at info@robertomarchetto.com, we might consider a business deal.

## Modules
The features currently implemented are:

* Multiple connectors support (Currenlty Oanda, IgIndex and FXMC) to receive prices and manage orders.
* Collects histotical price ticks.
* Supports multiple strategies written in Java, other languages like Python can be considered.
* Trading service written in Java and API exposed via REST, this allow to use any language as a client.
* Very basic market rick module which can be extended.
* A good balance of microservice architecture with Docker, without marking the system too complex to manage.
* Client in Python. The management, monitoring and data analysis is performed mostly with [Jupyter Notebook](http://jupyter.org).

## FAQ

### How can I install and use it?
The deployment requires some knowledge of Docker microservices and can be tricky, that's why I did not write an installation guide. If you have any profitable trading strategy you would like to automate please write me at info@robertomarchetto.com, we might consider a business deal.

### What is this platform aimed at?
The goal of the project is to run profitable strategies and have all the tools requried for analysis, backtest and optimisation. It was not designed for low latency however few milliseconds of lacency are manageable.

### Are you using it in production?
Yes I am, with a simple strategy which is generating decent profits. I plan to add more strategies in future, all the strategies are in a private separated repository.
