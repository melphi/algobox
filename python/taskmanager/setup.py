from os import path
from setuptools import find_packages, setup

CURRENT_PATH = path.dirname(path.abspath(__file__))

with open(CURRENT_PATH + '/requirements.txt', 'r') as file:
    REQUIREMENTS = file.read().splitlines()

setup(
    name='taskmanager',
    version='0.0.1',
    description='TaskManager',
    test_suite='tests',
    packages=find_packages(include=['src.*']),
    include_package_data=True,
    zip_safe=False,
    install_requires=REQUIREMENTS)
