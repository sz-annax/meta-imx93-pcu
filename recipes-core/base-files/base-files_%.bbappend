FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://colors.sh file://fstab file://fw_env.config file://my-echo-cancel.conf file://pcu_data.db"

do_install:append() {
    # colors.sh
    install -d ${D}${sysconfdir}/profile.d
    install -m 0755 ${WORKDIR}/sources/colors.sh ${D}${sysconfdir}/profile.d/colors.sh

    # fw_env.config
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/sources/fw_env.config ${D}${sysconfdir}

    # pcu_data.db
    install -d ${D}${sysconfdir}/pcu
    install -m 0644 ${WORKDIR}/sources/pcu_data.db ${D}${sysconfdir}/pcu

    # my-echo-cancel.conf
    install -d ${D}${sysconfdir}/pipewire/pipewire.conf.d
    install -m 0644 ${WORKDIR}/sources/my-echo-cancel.conf ${D}${sysconfdir}/pipewire/pipewire.conf.d

    install -d ${D}/boot/firmware
    install -d ${D}/mnt/param
    install -d ${D}/mnt/data/uploads
}
