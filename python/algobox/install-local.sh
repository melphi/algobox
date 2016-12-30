#!/usr/bin/env bash
python3 src/setup.py clean
python3 src/setup.py build
python3 src/setup.py install --user
