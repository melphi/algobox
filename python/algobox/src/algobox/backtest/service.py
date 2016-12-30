from connexion import App
import logging


logging.basicConfig(level=logging.INFO)
app = App(__name__)
app.add_api('swagger.yaml')
# set the WSGI application callable to allow using uWSGI
application = app.app

if __name__ == '__main__':
    app.run(port=8080, server='gevent')
