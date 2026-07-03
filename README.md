# osrsx-maven

A plain **Maven repository** for the osrsx ecosystem's `io.osrsx:*` artifacts, served anonymously over
`raw.githubusercontent.com/osrsx/osrsx-maven/main/` — **no token needed** to consume.

Currently hosts the SDK (`io.osrsx:osrsx-api`, `io.osrsx:osrsx-testkit`, the `io.osrsx.plugin` Gradle
plugin marker). Published from [`osrsx/osrsx-sdk`](https://github.com/osrsx/osrsx-sdk). Consume with:

```gradle
maven { url 'https://raw.githubusercontent.com/osrsx/osrsx-maven/main/' }
```

This repo holds only built artifacts — do not edit by hand; the producing repo's `publish` step writes here.
