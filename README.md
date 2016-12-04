# algobox

Algobox is an open source algorithmic trading software. The software is used in production but the source code is currently in a privare repository, I will push the source code by the first months of 2017.

If you have a profitable trading strategy you would like to automate please write me at info@robertomarchetto.com, we might consider a business deal.

## Modules
The features currently implemented are:

* Multiple connectors support (Currenlty Oanda, IgIndex and FXMC) to receive prices and manage orders.
* Collects histotical price ticks.
* Supports multiple strategies written in Java, other languages like Python can be considered.
* Trading service written in Java and API exposed via REST, this allow to use any language as a client.
* Client in Python. The management, monitoring and data analysis is performed mostly with [Jupyter Notebook](http://jupyter.org).
* Very basic market rick module which can be extended.

## FAQ
### Where is the source code?
At the moment it is on BitBucket, there are some part of the code I need to refactor before making it public (eg passwords, custom strategies). I plan to push here by the first months of 2017.

### How can I install and use it?
The deployment requires some knowledge of Docker microservices and can be tricky, that's why I did not write an installation guide. If you have any profitable trading strategy you would like to automate please write me at info@robertomarchetto.com, we might consider a business deal.

### What is this platform aimed for?
The goal of the project is to run profitable strategies and have all the tools requried for analysis, backtest and optimisation. It was not designed for low latency however few milliseconds of lacency are manageable.

### Are you using it in production?
Yes I am, with a simple strategy which is generating decent profits. I plan to add more strategies in future, all the strategies are in a private separated repository.
