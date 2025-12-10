SUMMARY = "PEI Python Application Service"
DESCRIPTION = "PEI Python application that runs as a systemd service."
LICENSE = "CLOSED"

SRC_URI = "git://git@github.com/sz-annax/pei-app.git;protocol=ssh;branch=main \
           file://pei-app.service \
           file://pei-app.sh \
           file://my-echo-cancel.conf \
        "

SRCREV = "325e6b1a63476c2b488a095158fb760e5c8eb589"

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

    # my-echo-cancel.conf
    install -d ${D}${sysconfdir}/pipewire/pipewire.conf.d
    install -m 0644 ${UNPACKDIR}/my-echo-cancel.conf ${D}${sysconfdir}/pipewire/pipewire.conf.d
}

FILES_${PN} = "${bindir}/pei-app.sh"

inherit systemd

SYSTEMD_SERVICE:${PN} = "pei-app.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
