FROM base/jre:1.0

ENV DELIVER_TRANSPORT_HOME="/home/deliver/deliver-transport"
ENV PATH="${DELIVER_TRANSPORT_HOME}/bin:${PATH}"

# 设置支持中文
RUN localedef -i zh_CN -f UTF-8 zh_CN.UTF-8
ENV LANG zh_CN.UTF-8
ENV LANGUAGE zh_CN.UTF-8
ENV LC_ALL zh_CN.UTF-8

COPY bin  ${DELIVER_TRANSPORT_HOME}/bin
COPY lib  ${DELIVER_TRANSPORT_HOME}/lib

EXPOSE 80/tcp

WORKDIR ${DELIVER_TRANSPORT_HOME}

ENTRYPOINT ["deliver-transport"]