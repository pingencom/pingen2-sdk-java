# Setup Instruction for Maven Publishing

## 0 Prerequisites

* Existing GitHub repository
* Maven Central account

## 1 GPG key - for signing artifacts

Generate a key – if you don't have one

```bash
gpg --gen-key
```

```
Name: Pingen GmbH
Mail: support@pingen.com
Passphrase: Enter a passphrase and store it securely
```

List keys to find your key ID

```bash
gpg --list-secret-keys --keyid-format=long
```

Export the private key (the whole block including headers)

```bash
gpg --export-secret-keys --armor YOUR_KEY_ID
```

This is your ASCII-armored GPG private key.

Store the private key in a secret / password manager

Hint: Store the entire output (including -----BEGIN PGP PRIVATE KEY BLOCK-----)

**Important**: Also upload your public key to a keyserver so Maven Central can verify (it can take a few minutes to propagate):

```bash
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
```

## 2 Set up Maven Central

MAVEN_CENTRAL_USERNAME and MAVEN_CENTRAL_TOKEN

Got to Maven Central Portal:
1. Go to Account → Generate User Token
2. The generated username → MAVEN_CENTRAL_USERNAME, the token → MAVEN_CENTRAL_TOKEN

## 3 Set up GitHub secrets

Go to your repo → Settings → Secrets and variables → Actions → New repository secret and add these four secrets:

```
GPG_PRIVATE_KEY=<the entire output of gpg --export-secret-keys --armor YOUR_KEY_ID>
GPG_PASSPHRASE=<the passphrase you used when creating the GPG key>
MAVEN_CENTRAL_USERNAME=<the username you got from Maven Central>
MAVEN_CENTRAL_TOKEN=<the token you got from Maven Central>
```
