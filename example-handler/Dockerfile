FROM veupathdb/vdi-handler-server

RUN mkdir "/opt/veupathdb"

COPY scripts/import.sh /opt/veupathdb/import
COPY scripts/install-meta.sh /opt/veupathdb/install-meta
COPY scripts/install-data.sh /opt/veupathdb/install-data
COPY scripts/uninstall.sh /opt/veupathdb/uninstall

RUN chmod +x /opt/veupathdb/import /opt/veupathdb/install-meta /opt/veupathdb/install-data /opt/veupathdb/uninstall