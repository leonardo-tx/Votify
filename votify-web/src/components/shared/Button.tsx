import Link, { LinkProps } from "next/link";
import {
  AnchorHTMLAttributes,
  ButtonHTMLAttributes,
  forwardRef,
  ReactNode,
  Ref,
} from "react";
import styles from "./styles/Button.module.css";

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  as?: "button";
};

type AnchorProps = AnchorHTMLAttributes<HTMLAnchorElement> & {
  as: "a";
};

type NextLinkProps = {
  as: "Link";
  href: LinkProps["href"];
} & Omit<AnchorHTMLAttributes<HTMLAnchorElement>, "href">;

type Props = {
  variant?: "solid" | "outline" | "text";
  scheme?: "primary";
  className?: string;
  children?: ReactNode;
} & (ButtonProps | AnchorProps | NextLinkProps);

const Button = forwardRef<HTMLButtonElement | HTMLAnchorElement, Props>(
  (
    { as = "button", variant = "solid", scheme, className, children, ...props },
    ref,
  ) => {
    const classNameBuilder = [className, styles["common"], styles[variant]];
    if (scheme !== undefined) {
      classNameBuilder.push(styles[scheme + "-" + variant]);
    }

    switch (as) {
      case "button":
        props = props as ButtonProps;
        if (props.disabled) classNameBuilder.push(styles["button-disabled"]);
        return (
          <button
            ref={ref as Ref<HTMLButtonElement>}
            draggable="false"
            className={classNameBuilder.join(" ")}
            {...props}
          >
            {children}
          </button>
        );
      case "a":
        props = props as AnchorProps;
        return (
          <a
            ref={ref as Ref<HTMLAnchorElement>}
            draggable="false"
            className={classNameBuilder.join(" ")}
            {...props}
          >
            {children}
          </a>
        );
      case "Link":
        props = props as LinkProps;
        return (
          <Link
            ref={ref as Ref<HTMLAnchorElement>}
            draggable="false"
            className={classNameBuilder.join(" ")}
            {...props}
          >
            {children}
          </Link>
        );
      default:
        const _exhaustiveCheck: never = as;
        return _exhaustiveCheck;
    }
  },
);

Button.displayName = "Button";
export default Button;
