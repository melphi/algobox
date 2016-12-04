# algobox

Algobox is an open source algorithmic trading software. The software is used in production but the source code is currently in a privare repository, I will push the source code by the first months of 2017.

If you have a profitable trading strategy you would like to automate please write me at info@robertomarchetto.com, we might consider a business deal.

## Modules
The functionalities currently implemented are:

* Supports multiple connectors (Currenlty Oanda, IgIndex and FXMC) to receive prices and send orders to.
* Collects histotical price ticks 
* Supports multiple strategies written in Java, other languages like Python can be considered.
* The tradind service written in Java and the API is exposed via REST, this allow to use any language as a client.
* The client is in Python. The management, monitoring and data analysis is performed mostly with [Jupyter Notebook](http://jupyter.org)

## FAQ
### Where is the source code?
At the moment it is on BitBucket, there are some part of the code I need to refactor before making it public (eg passwords, custom strategies). I plan to push here by the first months of 2017.

### How can I install and use it?
The deployment requires some knowledge of Docker microservices and can be tricky, that's why I did not write an installation guide. If you have any profitable trading strategy you would like to automate please write me at info@robertomarchetto.com, we might consider a business deal.

### What is this platform aimed for?
The goal of the project is to run profitable strategies and have all the tools requried to backtest and optimise them.

### Are you using it in production?
Yes I am, with a simple strategy which is generating decent profits. I plan to add more strategies in future, all the strategies are in a private separated repository.
