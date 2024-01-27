**NeoQuilt: NeoQuilt: Quilt Standard Libraries をよりアクセスしやすくする**

NeoQuilt は Quilt Standard Libraries のフォークであり、新しい機能を提供し、Fabric や Forge (近日公開) などのより多くの変更ローダーや新しいバージョンで実行できるように設計されています。 NeoQuilt は QSL と他のツール (Quilt Config など) のフォークにすぎず、変更ローダーはなく、これらの他のプラットフォームで Quilt の変更を単独で実行することはできません。

NeoQuilt には Quilt 固有のバージョンはありませんが、代わりに Quilt で動作する Fabric バージョンがあります。 また、近日中に Quilt Mappings から MojMaps へ、QuiltLoom から FabricLoom へ移行する予定です。

NeoQuilt の Fabric バージョンは、従来の Fabric API に依存し、それを使用して実行されます。QSL ライブラリの多くは Fabric API のコピーにすぎませんでしたが、これで完了ではないため、重複コードを減らすために何らかの支援が必要です。 他の modloader にも Fabric API が移植されてバンドルされますが、Fabric では個別にインストールする必要があります。 Quilt のレガシーなものの一部は残りますが、その多くは、一時的に保持することが重要であると感じない限り、または Fabric API バージョンがない限り、Quilt が削除すると削除されます。 開発者には、可能な限り net.fabricmc 内のパッケージを使用することをお勧めします。 特にオリジナルの QSL の開発が停止した場合には、org.quiltmc パッケージよりも「jp.mikumikudance.neoquilt」の方が望ましいと考えられます。

**声明**
私たちは、誰でも変更を加えてプレイできるようにする必要があると考え、これを行うことにしました。 コードに対する人為的な制限により、平均的なユーザーのエクスペリエンスが悪化します。 この分割により、以前は Fabric で動作していた多くの変更が Fabric では動作しなくなりました。 QuiltMC チームは、他のほとんどの修正ローダーのチームとも問題を引き起こし、私たちが同意しないことを行ってきました。 同時に、新しいバージョンに遅れをとり、多くの MOD が立ち往生したままになりました。 私たちは、NeoQuilt でこれらの問題を修正することを目指しています。彼らが反対していた修正ローダーに移植し、開発者とプレイヤーに権限を与え、政治から距離を置き、代わりに新しいバージョンを導入し、新機能を導入し、他のものとうまく連携することに焦点を当てます。 修正ローダー。 私たちは誰からのプルリクエストも歓迎しており、アイデアを非常にオープンに受け入れ、官僚主義には強く反対しています。だからこそ、誰でも簡単にほとんど何でも送信できるようにしており、それが機能する限りは、初音ミクが想像したように、ゲートキーピングなしで受け入れられるのです。 マインクラフトを作成しました。
https://discord.gg/FM2qJnCFwK
