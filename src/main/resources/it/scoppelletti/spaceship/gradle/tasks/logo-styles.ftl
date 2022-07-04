.library-name a {
    position: relative;
    --logo-width: 70px;
    margin-left: calc(var(--logo-width) + 5px);
}

.library-name a::before {
    content: '';
    background: url("${logoUrl}") center no-repeat;
    background-size: contain;
    position: absolute;
    width: var(--logo-width);
    height: 50px;
    top: -18px;
    left: calc(-1 * var(--logo-width) - 5px);
}
