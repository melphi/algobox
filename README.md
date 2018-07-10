[![Build Status](https://travis-ci.org/melphi/algobox.svg?branch=master)](https://travis-ci.org/melphi/algobox)
[![Code Climate](https://codeclimate.com/github/melphi/algobox/badges/gpa.svg)](https://codeclimate.com/github/melphi/algobox)

# algobox

Algobox is an open source algorithmic trading software. The software is used in production however the profitable strategies are in a privare repository.

## Modules
The features currently implemented are:

* Multiple connectors support (Currenlty Oanda, IgIndex and FXMC) to receive prices and manage orders.
* Collects histotical price ticks.
* Supports multiple strategies written in Java, other languages like Python can be considered.
* Trading service written in Java and API exposed via REST, this allow to use any language as a client.
* Very basic market rick module which can be extended.
* A good balance of microservice architecture with Docker, without marking the system too complex to manage.
* Client in Python. The management, monitoring and data analysis is performed mostly with [Jupyter Notebook](http://jupyter.org).
