FROM dainco/algobox-base-jupyter

# Environment variables.
# Use https
ENV USE_HTTPS=true
# Resolves missing graphical server related errors.
ENV MPLBACKEND=agg

# Install python requirements.
COPY src /opt/src/algobox
WORKDIR /opt/src/algobox
RUN pip install --no-cache-dir -r requirements.txt

# Install algobox.
RUN python3 setup.py test
RUN python3 setup.py install
COPY jupyter/* /home/$NB_USER/work/notebooks/

# Finish.
USER root
RUN rm -fr /opt/src
USER $NB_USER
WORKDIR /home/$NB_USER/work
