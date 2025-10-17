DESCRIPTION = "Mount /mnt/data/logs to /var/log/journal using systemd mount unit"
LICENSE = "CLOSED"
PR = "r0"

SRC_URI = "file://var-log-journal.mount \
           file://machine-id-persist.sh \
           file://machine-id-persist.service \
          "

S = "${UNPACKDIR}"

inherit systemd

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/var-log-journal.mount ${D}${systemd_system_unitdir}/

    install -d ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/machine-id-persist.sh ${D}${sbindir}/

    install -m 0644 ${UNPACKDIR}/machine-id-persist.service ${D}${systemd_system_unitdir}/
}

SYSTEMD_SERVICE:${PN} = "machine-id-persist.service var-log-journal.mount"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
