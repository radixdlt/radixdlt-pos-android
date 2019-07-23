[![License MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

# Radix DLT Android POS

The Radix DLT Android POS currently works with _BETANET_ universe and interacts with it
by making full use of the latest tag release of the [radixdlt-java](https://github.com/radixdlt/radixdlt-java/tree/v21-beta) library.

## WARNING:

A 100% reliable _BETANET_ is not yet released. Building against this is currently highly discouraged as things may not work as intended.
We expect to release a stable _BETANET_ in the coming weeks!

Build is currently running using a SNAPSHOT of the latest betanet client library rc/1.0.0-beta which may prove unstable or be reset at any given time.
This can be changed in the Dependencies.kt file. We will soon move to a release/betanet branch.

<img src="art/radix_pos_welcome.jpg" width="200">&nbsp;
<img src="art/radix_pos_start.jpg" width="200">&nbsp;
<img  src="art/radix_pos_amount.jpg" width="200">&nbsp;
<img  src="art/radix_pos_tap_scan.jpg" width="200">

## Android development

 * Written in [Kotlin](https://kotlinlang.org/)
 * Uses [Architecture Components](https://developer.android.com/topic/libraries/architecture/): ViewModel, LiveData
 * Uses [RxJava 2](https://github.com/ReactiveX/RxJava) (Included by default by the radixdlt libs)

## Development setup

Use Android Studio 3.4.1 (or newer) to be able to build the app.

In order to flash and use your own nfc cards, please see the [radixdlt-card-applet](https://github.com/radixdlt/radixdlt-card-applet) project.

Cards and devices which will run this app *must* support extended length APDU format

The current implementation of the POS app is currently developed to listen to one type of token.
This means that if a token has been created in the radix universe and this is the token that we want to accept,
this must be set in the [Constants.kt](https://github.com/radixdlt/radixdlt-pos-android/blob/master/app/src/main/java/com/radixdlt/android/apps/pos/util/Constants.kt) file for both the `TOKEN_REFERENCE_ADDRESS` and `TOKEN_REFERENCE_SYMBOL` constants.

`const val TOKEN_REFERENCE_ADDRESS = “JGPU2M7Wss6C3TjAtt3BaLESHGoWWCb5sKw5eQdsfHVk3CuPjpf”`  
`const val TOKEN_REFERENCE_SYMBOL = “USD”`

## Code style

This project uses [ktlint](https://github.com/pinterest/ktlint) via [Gradle](https://gradle.org/) dependency.
To check code style - `gradle ktlint` (it's also bound to `gradle check`).

## Contribute

Contributions are welcome, we simply ask to:

* Fork the codebase
* Make changes
* Submit a pull request for review

When contributing to this repository, we recommend discussing with the development team the change you wish to make using a [GitHub issue](https://github.com/radixdlt/radixdlt-pos-android/issues) before making changes.

Please follow our [Code of Conduct](CODE_OF_CONDUCT.md) in all your interactions with the project.

## Links

| Link | Description |
| :----- | :------ |
[radixdlt.com](https://radixdlt.com/) | Radix DLT Homepage
[documentation](https://docs.radixdlt.com/) | Radix Knowledge Base
[forum](https://forum.radixdlt.com/) | Radix Technical Forum
[@radixdlt](https://twitter.com/radixdlt) | Follow Radix DLT on Twitter

## Have a question?

If you need any information, please visit our [GitHub Issues](https://github.com/radixdlt/radixdlt-pos-android/issues)

## License

Radix DLT Android POS is released under the [MIT License](LICENSE).
