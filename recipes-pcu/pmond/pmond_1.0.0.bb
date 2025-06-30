DESCRIPTION = "PMON2 Application Management Services"

SRC_URI = "file://pmond.service \
           file://config.yml \
           file://pmond \
        "

LICENSE = "CLOSED"

do_install() {
    # /usr/bin/pmond
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/pmond ${D}${bindir}

    # /etc/pmond/config.yml
    install -d ${D}${sysconfdir}/pmond
    install -m 0644 ${UNPACKDIR}/config.yml ${D}${sysconfdir}/pmond

    # /usb/lib/systemd/system/pmond.service
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/pmond.service ${D}${systemd_unitdir}/system
}

FILES_${PN} = "${bindir}/pmond ${sysconfdir}/pmond/config.yml"

inherit systemd

SYSTEMD_SERVICE:${PN} = "pmond.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
