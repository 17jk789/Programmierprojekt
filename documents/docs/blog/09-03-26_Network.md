# Table of Contents


# Basics
## Formal concepts of networking
Whenever you want to access the same data on different devices without carrying it (the data) from device to device, with, let’s say, a thumb drive, you have multiple options for achieving this.

![Peer To Peer Versus Client-Server Architecture](/documents/images/docs/blog/09-03-26_Network/shared_ressouce_possible_approaches.svg)

<!-- I tried to get this embeddet image to show up, but it just wouldnt
<img src="/documents/images/docs/blog/09-03-26_Network/shared_ressouce_possible_approaches.svg" alt="Peer To Peer Versus Client-Server Architecture" width="200"/>
-->

**Peer to Peer** enables users to share content (files, messages, ...).
While it does not rely on a central server, it comes with the big trade-of that the device storing the data you want to access has to be reachable and online.
Today Peer to Peer is still widely used with VoIP and decentralized networks for censorship-resistant transactions formaly known as the blockchain.

The **Client-Server** model evolved into the sophisticated "web-services" we know and use daily.
One central powerful computer (the server) stores data and allows external devices (clients) to access it.

For our course "Programmierprojekt" we are required to use the Client-Server model.
