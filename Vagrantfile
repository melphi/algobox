$update_channel = "stable"
$image_version = "current"

Vagrant.configure("2") do |config|
    config.ssh.forward_agent = true
    config.ssh.insert_key = false
    config.vm.box = "coreos-%s" % $update_channel
    config.vm.box_check_update = false
    config.vm.box_url = "https://storage.googleapis.com/%s.release.core-os.net/amd64-usr/%s/coreos_production_vagrant.json" % [$update_channel, $image_version]
    config.vm.network "private_network", ip: "192.168.33.10"
    config.vm.synced_folder ".", "/vagrant", disabled: true

    config.vm.provider :virtualbox do |v|
        v.check_guest_additions = false
        v.cpus = 2
        v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
        v.customize ["modifyvm", :id, "--ioapic", "on"]
        v.memory = 1024
        v.name = "algobox.dev"
    end
end

