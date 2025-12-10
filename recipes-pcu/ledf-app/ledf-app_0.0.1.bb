SUMMARY = "LEDF Python Application Service"
DESCRIPTION = "LEDF Python application that runs as a systemd service."
LICENSE = "CLOSED"

SRC_URI = "git://git@github.com/sz-annax/ledf-app.git;protocol=ssh;branch=main \
           file://ledf-app.service \
           file://my-echo-cancel.conf \
        "

SRCREV = "6eb37b9dbc609fd4521d83ab58d2d2eb949ae868"

S = "${WORKDIR}/git"

do_install() {
    rm -rf ${S}/docs ${S}/script

    # /usr/share/pei-app
    install -d ${D}${datadir}/ledf-app
    cp -r ${S}/* ${D}${datadir}/ledf-app/

    # systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/ledf-app.service ${D}${systemd_system_unitdir}/

    # my-echo-cancel.conf
    install -d ${D}${sysconfdir}/pipewire/pipewire.conf.d
    install -m 0644 ${UNPACKDIR}/my-echo-cancel.conf ${D}${sysconfdir}/pipewire/pipewire.conf.d
}

FILES_${PN} = "${bindir}/${PN}.sh"

inherit systemd

SYSTEMD_SERVICE:${PN} = "ledf-app.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
