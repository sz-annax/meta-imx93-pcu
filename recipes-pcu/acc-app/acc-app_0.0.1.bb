SUMMARY = "ACC Python Application Service"
DESCRIPTION = "ACC Python application that runs as a systemd service."
LICENSE = "CLOSED"

SRC_URI = "git://git@github.com/sz-annax/acc-hc.git;protocol=ssh;branch=main \
           file://acc-app.service \
           file://my-echo-cancel.conf \
        "
SRCREV = "248170f6e30aeabf61f9ac57bdeea639fff10a5f"

S = "${WORKDIR}/git"

do_install() {
    # delete unnecessary files and folders
    rm -rf ${S}/docs ${S}/script

    # /usr/share/acc-app
    install -d ${D}${datadir}/${PN}
    cp -r ${S}/* ${D}${datadir}/${PN}/

    # systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/${PN}.service ${D}${systemd_system_unitdir}/

    # my-echo-cancel.conf
    install -d ${D}${sysconfdir}/pipewire/pipewire.conf.d
    install -m 0644 ${UNPACKDIR}/my-echo-cancel.conf ${D}${sysconfdir}/pipewire/pipewire.conf.d
}

FILES_${PN} = "${bindir}/${PN}.sh"

inherit systemd

SYSTEMD_SERVICE:${PN} = "${PN}.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
