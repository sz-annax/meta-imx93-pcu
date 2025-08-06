SUMMARY = "PEI Python Application Service"
DESCRIPTION = "PEI Python application that runs as a systemd service."
LICENSE = "CLOSED"

SRC_URI = "git://git@github.com/sz-annax/pei-app.git;protocol=ssh;branch=main \
           file://pei-app.service \
           file://pei-app.sh \
        "

SRCREV = "aa23f425d370ca197a955221aba184c08ff65376"

S = "${WORKDIR}/git"

do_install() {
    rm -rf ${S}/docs ${S}/script

    # /usr/share/pei-app
    install -d ${D}${datadir}/pei-app
    cp -r ${S}/* ${D}${datadir}/pei-app/

    # /usr/bin/pei-app.sh
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/pei-app.sh ${D}${bindir}

    # systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/pei-app.service ${D}${systemd_system_unitdir}/
}

FILES_${PN} = "${bindir}/pei-app.sh"

inherit systemd

SYSTEMD_SERVICE:${PN} = "pei-app.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
